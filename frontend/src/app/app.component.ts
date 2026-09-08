import { Component, ElementRef, ViewChild } from '@angular/core';
import { AnalyzerError, AnalyzerService, AnalysisResult } from './analyzer.service';
import { FooterComponent } from './footer/footer.component';
import { LanguageService } from './language.service';
import { Language, TranslationKey } from './translations';

type ResultList = 'followers' | 'following' | 'mutual' | 'notFollowingBack' | 'youDoNotFollowBack';

@Component({
  selector: 'app-root',
  imports: [FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private static readonly maxFileSizeBytes = 10 * 1024 * 1024;

  @ViewChild('followersInput') followersInput?: ElementRef<HTMLInputElement>;
  @ViewChild('followingInput') followingInput?: ElementRef<HTMLInputElement>;

  followersFile: File | null = null;
  followingFile: File | null = null;
  loading = false;
  errorMessage = '';
  errorCode = '';
  result: AnalysisResult | null = null;
  selectedList: ResultList = 'mutual';
  showExportHelp = false;

  constructor(
    private readonly analyzerService: AnalyzerService,
    private readonly languageService: LanguageService
  ) {}

  t(key: TranslationKey): string {
    return this.languageService.t(key);
  }

  get language(): Language {
    return this.languageService.language;
  }

  setLanguage(language: Language): void {
    this.languageService.setLanguage(language);
  }

  get canAnalyze(): boolean {
    return (
      !!this.followersFile &&
      !!this.followingFile &&
      !this.loading &&
      this.isJsonFile(this.followersFile) &&
      this.isJsonFile(this.followingFile) &&
      !this.isTooLarge(this.followersFile) &&
      !this.isTooLarge(this.followingFile)
    );
  }

  onFollowersSelected(event: Event): void {
    this.followersFile = this.readSelectedFile(event);
  }

  onFollowingSelected(event: Event): void {
    this.followingFile = this.readSelectedFile(event);
  }

  analyze(): void {
    if (!this.canAnalyze || !this.followersFile || !this.followingFile) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.errorCode = '';
    this.result = null;

    this.analyzerService.analyze(this.followersFile, this.followingFile).subscribe({
      next: (result) => {
        this.result = result;
        this.selectedList = 'mutual';
        this.loading = false;
      },
      error: (error: AnalyzerError) => {
        this.result = null;
        this.errorCode = error.code ?? '';
        this.errorMessage = error.message;
        this.loading = false;
      }
    });
  }

  clear(): void {
    this.followersFile = null;
    this.followingFile = null;
    this.loading = false;
    this.errorMessage = '';
    this.errorCode = '';
    this.result = null;
    this.selectedList = 'mutual';
    if (this.followersInput) {
      this.followersInput.nativeElement.value = '';
    }
    if (this.followingInput) {
      this.followingInput.nativeElement.value = '';
    }
  }

  showList(list: ResultList): void {
    this.selectedList = list;
  }

  toggleExportHelp(): void {
    this.showExportHelp = !this.showExportHelp;
  }

  get selectedUsernames(): string[] {
    if (!this.result) {
      return [];
    }

    switch (this.selectedList) {
      case 'followers':
        return this.result.followers ?? [];
      case 'following':
        return this.result.following ?? [];
      case 'mutual':
        return this.result.mutualFollowers ?? [];
      case 'notFollowingBack':
        return this.result.notFollowingBack ?? [];
      case 'youDoNotFollowBack':
        return this.result.youDoNotFollowBack ?? [];
    }
  }

  get selectedEmptyMessage(): string {
    switch (this.selectedList) {
      case 'followers':
        return this.t('emptyFollowers');
      case 'following':
        return this.t('emptyFollowing');
      case 'mutual':
        return this.t('emptyMutual');
      case 'notFollowingBack':
        return this.t('emptyNotFollowingBack');
      case 'youDoNotFollowBack':
        return this.t('emptyYouDoNotFollowBack');
    }
  }

  get selectedListTitle(): string {
    switch (this.selectedList) {
      case 'followers':
        return this.t('titleFollowers');
      case 'following':
        return this.t('titleFollowing');
      case 'mutual':
        return this.t('titleMutual');
      case 'notFollowingBack':
        return this.t('titleNotFollowingBack');
      case 'youDoNotFollowBack':
        return this.t('titleYouDoNotFollowBack');
    }
  }

  get displayedError(): string {
    if (!this.errorMessage && !this.errorCode) {
      return '';
    }

    if (this.errorMessage.includes('conectar') || this.errorMessage.includes('connect')) {
      return this.t('errorConnection');
    }

    const translated = this.errorFromCode(this.errorCode, this.errorMessage);
    return translated || this.t('errorGeneric');
  }

  isFileInvalid(file: File | null): boolean {
    return !!file && (!this.isJsonFile(file) || this.isTooLarge(file));
  }

  fileStatus(file: File | null, kind: 'followers' | 'following'): string {
    if (!file) {
      return kind === 'followers' ? this.t('noFollowersFile') : this.t('noFollowingFile');
    }
    if (!this.isJsonFile(file)) {
      return `${file.name} ${this.t('notJson')}`;
    }
    if (this.isTooLarge(file)) {
      return `${file.name} ${this.t('tooLarge')}`;
    }
    return file.name;
  }

  private errorFromCode(code: string, message: string): string {
    const isFollowing = message.toLowerCase().includes('following');
    switch (code) {
      case 'MISSING_FILE':
        return this.t(isFollowing ? 'errorMissingFollowing' : 'errorMissingFollowers');
      case 'EMPTY_FILE':
        return this.t(isFollowing ? 'errorEmptyFollowing' : 'errorEmptyFollowers');
      case 'FILE_TOO_LARGE':
        return this.t(isFollowing ? 'errorTooLargeFollowing' : 'errorTooLargeFollowers');
      case 'NOT_JSON':
        return this.t(isFollowing ? 'errorNotJsonFollowing' : 'errorNotJsonFollowers');
      case 'INVALID_JSON':
        return this.t(isFollowing ? 'errorInvalidJsonFollowing' : 'errorInvalidJsonFollowers');
      case 'INVALID_STRUCTURE':
        return this.t(isFollowing ? 'errorInvalidStructureFollowing' : 'errorInvalidStructureFollowers');
      case 'NO_USERS':
        return this.t(isFollowing ? 'errorNoUsersFollowing' : 'errorNoUsersFollowers');
      default:
        return '';
    }
  }

  private readSelectedFile(event: Event): File | null {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length > 0 ? input.files[0] : null;
    return file;
  }

  private isJsonFile(file: File): boolean {
    return file.name.toLowerCase().endsWith('.json');
  }

  private isTooLarge(file: File): boolean {
    return file.size > AppComponent.maxFileSizeBytes;
  }
}
