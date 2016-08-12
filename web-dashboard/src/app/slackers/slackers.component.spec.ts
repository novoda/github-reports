/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { addProviders, async, inject } from '@angular/core/testing';
import { SlackersComponent } from './slackers.component';

describe('Component: Slackers', () => {
  it('should create an instance', () => {
    let component = new SlackersComponent();
    expect(component).toBeTruthy();
  });
});
