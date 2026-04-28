import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorBicicletaComponent } from './selector-bicicleta.component';

describe('SelectorBicicletaComponent', () => {
  let component: SelectorBicicletaComponent;
  let fixture: ComponentFixture<SelectorBicicletaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectorBicicletaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectorBicicletaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
