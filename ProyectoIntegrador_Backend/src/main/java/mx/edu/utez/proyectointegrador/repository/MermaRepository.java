package mx.edu.utez.proyectointegrador.repository;

import mx.edu.utez.proyectointegrador.model.Merma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MermaRepository extends JpaRepository<Merma, Long> {

    void deleteByProductoId(Long productoId);
}
