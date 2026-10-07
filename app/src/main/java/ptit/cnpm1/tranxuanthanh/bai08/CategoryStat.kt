package ptit.cnpm1.tranxuanthanh.bai08

/** Mục thu/chi kèm tổng tiền trong kỳ được chọn. */
data class CategoryStat(
    override var id: Int = 0,
    override var name: String = "",
    override var icon: String = Icons.DEFAULT,
    override var note: String = "",
    override var type: CategoryType = CategoryType(),
    override var parent: Category? = null,
    var total: Float = 0f
) : Category(id, name, icon, note, type, parent) {

    constructor(category: Category, total: Float) : this(
        id = category.id,
        name = category.name,
        icon = category.icon,
        note = category.note,
        type = category.type,
        parent = category.parent,
        total = total
    )
}
