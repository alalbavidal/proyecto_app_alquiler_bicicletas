import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorAccesorioComponent } from './selector-accesorio.component';

describe('SelectorAccesorioComponent', () => {
  let component: SelectorAccesorioComponent;
  let fixture: ComponentFixture<SelectorAccesorioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectorAccesorioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectorAccesorioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
