package pl.wojma.funwithxmls.core.ports;

import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.core.model.StructuredNode;

/**
 * Alias komparatora XML zachowany dla kompatybilności.
 *
 * @deprecated użyj {@link pl.wojma.funwithxmls.core.ports.StructuredComparator}
 */
@Deprecated
public interface XmlComparator extends pl.wojma.funwithxmls.core.ports.StructuredComparator {
    @Override
    ComparisonResult compare(StructuredNode left, StructuredNode right, String leftSource, String rightSource, ComparisonMode mode);
}
