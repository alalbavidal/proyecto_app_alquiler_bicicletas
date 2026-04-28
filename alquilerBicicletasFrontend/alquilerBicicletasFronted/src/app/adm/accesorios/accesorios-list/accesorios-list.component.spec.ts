import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccesoriosListComponent } from './accesorios-list.component';

describe('AccesoriosListComponent', () => {
  let component: AccesoriosListComponent;
  let fixture: ComponentFixture<AccesoriosListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccesoriosListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccesoriosListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
