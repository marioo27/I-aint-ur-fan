/**
 * Capture EN/ES screenshots for the README.
 * Requires the Angular app at http://localhost:4200 and puppeteer-core
 * (uses system Chrome).
 *
 *   npm install puppeteer-core
 *   node docs/capture-screens.mjs
 */
import puppeteer from 'puppeteer-core';
import { mkdir } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const outDir = path.join(__dirname, 'screenshots');
const chrome =
  process.env.CHROME_PATH ||
  'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

await mkdir(outDir, { recursive: true });

const browser = await puppeteer.launch({
  executablePath: chrome,
  headless: true,
  args: ['--hide-scrollbars', '--disable-gpu']
});

async function shot(name, url, viewport, before) {
  const page = await browser.newPage();
  await page.setViewport(viewport);
  await page.goto(url, { waitUntil: 'networkidle0', timeout: 60000 });
  if (before) await before(page);
  const file = path.join(outDir, name);
  await page.screenshot({ path: file, type: 'png' });
  await page.close();
  console.log('wrote', file);
}

try {
  await shot('app-en-hero.png', 'http://localhost:4200/?lang=en', {
    width: 1440,
    height: 900
  });
  await shot('app-es-hero.png', 'http://localhost:4200/?lang=es', {
    width: 1440,
    height: 900
  });
  await shot(
    'app-en-help.png',
    'http://localhost:4200/?lang=en&help=1',
    { width: 1440, height: 1100 },
    async (page) => {
      await page.waitForSelector('#export-help', { timeout: 15000 });
      await page.$eval('#how', (el) => el.scrollIntoView({ block: 'start' }));
      await new Promise((r) => setTimeout(r, 500));
    }
  );
  await shot(
    'app-es-help.png',
    'http://localhost:4200/?lang=es&help=1',
    { width: 1440, height: 1100 },
    async (page) => {
      await page.waitForSelector('#export-help', { timeout: 15000 });
      await page.$eval('#how', (el) => el.scrollIntoView({ block: 'start' }));
      await new Promise((r) => setTimeout(r, 500));
    }
  );
} finally {
  await browser.close();
}
