import { Component, ElementRef, ViewChild } from '@angular/core';
import { AnalyzerError, AnalyzerService, AnalysisResult } from './analyzer.service';

type ResultList = 'followers' | 'following' | 'mutual' | 'notFollowingBack' | 'youDoNotFollowBack';

@Component({
  selector: 'app-root',
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
  result: AnalysisResult | null = null;
  selectedList: ResultList = 'mutual';
  showExportHelp = false;

  constructor(private readonly analyzerService: AnalyzerService) {}

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
    this.result = null;

    this.analyzerService.analyze(this.followersFile, this.followingFile).subscribe({
      next: (result) => {
        this.result = result;
        this.selectedList = 'mutual';
        this.loading = false;
      },
      error: (error: AnalyzerError) => {
        this.result = null;
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
        return 'Esta lista de seguidores está vacía.';
      case 'following':
        return 'Esta lista de cuentas que sigues está vacía.';
      case 'mutual':
        return 'No hay cuentas que te sigan de vuelta. Esta lista está vacía.';
      case 'notFollowingBack':
        return 'Todas las cuentas que sigues también te siguen. Esta lista está vacía.';
      case 'youDoNotFollowBack':
        return 'Sigues a todas las cuentas que te siguen. Esta lista está vacía.';
    }
  }

  get selectedListTitle(): string {
    switch (this.selectedList) {
      case 'followers':
        return 'Seguidores · cuentas que te siguen';
      case 'following':
        return 'Siguiendo · cuentas que sigues';
      case 'mutual':
        return 'Amigos · cuentas que sigues y te siguen de vuelta';
      case 'notFollowingBack':
        return 'Fans · cuentas que sigues y no te siguen de vuelta';
      case 'youDoNotFollowBack':
        return 'Admiradores · cuentas que te siguen y tú no sigues';
    }
  }

  isFileInvalid(file: File | null): boolean {
    return !!file && (!this.isJsonFile(file) || this.isTooLarge(file));
  }

  fileStatus(file: File | null, label: string): string {
    if (!file) {
      return `Ningún archivo de ${label} seleccionado`;
    }
    if (!this.isJsonFile(file)) {
      return `${file.name} no es un archivo JSON`;
    }
    if (this.isTooLarge(file)) {
      return `${file.name} supera el tamaño máximo de 10 MB`;
    }
    return file.name;
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
