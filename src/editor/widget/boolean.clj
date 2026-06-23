(ns editor.widget.boolean
  (:require [ui.check-box :as check-box]))

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))

(defn value
  [_ widget _schemas]
  (check-box/checked? widget))
