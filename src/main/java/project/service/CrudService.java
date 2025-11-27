package project.service;

import java.util.List;

/**
 * @param <D> dto
 * @param <CD> create dto
 * @param <UD> update dto
 * @param <I> id
 */
public interface CrudService<D, CD, UD, I> {

    D create(CD dto);

    D get(I id);

    List<D> getAll(String search);

    D update(UD dto);

    void delete(I id);


}