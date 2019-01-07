package util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {


	@Setter(value = AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long identity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date createdOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date lastModifiedOn;
	public final boolean isNew() {
        return this.identity == null;
    }

    @PrePersist
    public void prePersist() {
        this.audit();
    }

    @PreUpdate
    public void preUpdate() {
        this.audit();
    }
    
    private void audit() {
        if (this.isNew()) {
            this.createdOn = new Date();
        }
        this.lastModifiedOn = new Date();
    }
}
