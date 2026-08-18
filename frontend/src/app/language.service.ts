import { Injectable } from '@angular/core';
import { Language, TranslationKey, translations } from './translations';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private static readonly storageKey = 'iaint-ur-fan-language';

  language: Language = this.readStoredLanguage();

  t(key: TranslationKey): string {
    return translations[this.language][key];
  }

  setLanguage(language: Language): void {
    this.language = language;
    localStorage.setItem(LanguageService.storageKey, language);
    document.documentElement.lang = language;
  }

  private readStoredLanguage(): Language {
    const stored = localStorage.getItem(LanguageService.storageKey);
    if (stored === 'en' || stored === 'es') {
      document.documentElement.lang = stored;
      return stored;
    }

    const browser = navigator.language.toLowerCase().startsWith('en') ? 'en' : 'es';
    document.documentElement.lang = browser;
    return browser;
  }
}
