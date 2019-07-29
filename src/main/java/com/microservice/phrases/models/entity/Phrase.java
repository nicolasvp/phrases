package com.microservice.phrases.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="phrases")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Phrase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message="can't be empty")
	@Size(min=1, max=200, message="must have between 1 and 200 characters")
	private String body;
	
	@NotNull(message="can't be empty")
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="author_id")
	private Author author;
	
	@NotNull(message="can't be empty")
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	private Type type;
	
	@NotNull(message="can't be empty")
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="image_id", referencedColumnName="id")
	private Image image;
	
	@Column(name="likes_counter")
	private Long likesCounter;
	
	@Column(name="created_at")
	@Temporal(TemporalType.DATE)
	private Date createdAt;
	
	public Phrase() {
		super();
	}

	public Phrase(String body, Author author, Type type,Image image, Long likesCounter, Date createdAt) {
		this.body = body;
		this.author = author;
		this.type = type;
		this.image = image;
		this.likesCounter = likesCounter;
		this.createdAt = createdAt;
	}

	// Set current date for createdAt field
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Long getLikesCounter() {
		return likesCounter;
	}

	public void setLikesCounter(Long likesCounter) {
		this.likesCounter = likesCounter;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
