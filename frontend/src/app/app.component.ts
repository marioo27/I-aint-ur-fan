import { Component, ElementRef, ViewChild } from '@angular/core';
import { AnalyzerError, AnalyzerService, AnalysisResult } from './analyzer.service';

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
    if (this.followersInput) {
      this.followersInput.nativeElement.value = '';
    }
    if (this.followingInput) {
      this.followingInput.nativeElement.value = '';
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
