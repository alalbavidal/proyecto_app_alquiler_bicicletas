import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BicicletaFormComponent } from './bicicleta-form.component';

describe('BicicletaFormComponent', () => {
  let component: BicicletaFormComponent;
  let fixture: ComponentFixture<BicicletaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BicicletaFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BicicletaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
