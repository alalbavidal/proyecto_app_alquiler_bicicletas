import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorTarifaComponent } from './selector-tarifa.component';

describe('SelectorTarifaComponent', () => {
  let component: SelectorTarifaComponent;
  let fixture: ComponentFixture<SelectorTarifaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectorTarifaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectorTarifaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
