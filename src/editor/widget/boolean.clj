(ns editor.widget.boolean
  (:require [scene2d.ui.check-box :as check-box]
            [scene2d.ui.check-box.is-checked :as checked?]))

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))

(defn value
  [_ widget _schemas]
  (checked?/f widget))
