import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TarifaFormComponent } from './tarifa-form.component';

describe('TarifaFormComponent', () => {
  let component: TarifaFormComponent;
  let fixture: ComponentFixture<TarifaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarifaFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TarifaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
