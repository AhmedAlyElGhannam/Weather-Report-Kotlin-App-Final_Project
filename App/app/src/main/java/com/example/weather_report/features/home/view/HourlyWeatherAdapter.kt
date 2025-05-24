//package com.example.weather_report.features.home.view
//
//class HourlyWeatherAdapter(var myListener : (Product) -> Unit):
//    ListAdapter<Product, AllProductsAdapter.ProductViewHolder>(ProductDiffUtill())
//{
//    lateinit var context :Context
//
//    lateinit var binding : AllItemBinding
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val inflater:LayoutInflater=LayoutInflater.from(parent.context)
//        context= parent.context
//        binding = AllItemBinding.inflate(inflater, parent, false)
//        return ProductViewHolder(binding)
//
//
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        val currentObj = getItem(position)
//        holder.binding.title.text = currentObj.title
//        holder.binding.price.text = currentObj.price.toString()
//        holder.binding.description.text = currentObj.description
//        holder.binding.ratingBar.rating = currentObj.rating.toFloat()
////        holder.binding.rec.setOnClickListener {
////            myListener.invoke(currentObj)
////        }
//        holder.binding.delBtn.setOnClickListener {
//            myListener.invoke(currentObj)
//        }
//        Glide.with(context).load(currentObj.thumbnail).into(holder.binding.imgv);
//    }
//
//    class ProductViewHolder(var binding: AllItemBinding) : RecyclerView.ViewHolder(binding.root)
//}