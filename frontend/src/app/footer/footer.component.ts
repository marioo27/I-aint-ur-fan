import { Component } from '@angular/core';
import { LanguageService } from '../language.service';
import { TranslationKey } from '../translations';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  constructor(private readonly languageService: LanguageService) {}

  t(key: TranslationKey): string {
    return this.languageService.t(key);
  }
}
