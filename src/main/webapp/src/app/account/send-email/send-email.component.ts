import { Component, OnInit } from '@angular/core';
import { Image } from "primeng/image";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-send-email',
  imports: [
    Image,
    Button,
    TranslatePipe
  ],
  templateUrl: './send-email.component.html',
  standalone: true,
  styleUrl: './send-email.component.scss'
})
export class SendEmailComponent implements OnInit {
  userEmail: string = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.userEmail = params.get('email') || '';
    });
  }

  openEmailClient(): void {
    if (this.userEmail) {
      window.location.href = `mailto:`;
    } else {
      console.warn('No email address provided to open email client.');
    }
  }
}
