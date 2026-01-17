(ns moon.info)

(defmulti text (fn [object ctx]
                 (cond (:item/slot object)
                       :info/item
                       :else
                       :info/entity)))

