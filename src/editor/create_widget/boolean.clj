(ns editor.create-widget.boolean
  (:require [scene2d.ui.check-box :as check-box]))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))
