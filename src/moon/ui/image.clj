(ns moon.ui.image
  (:require [gdl.ui.image :as image]
            [gdl.ui.widget :as widget]
            [moon.ui.actor :as actor]
            [gdl.utils.align :as align]
            [gdl.utils.scaling :as scaling]))

(defn create
  [{:keys [image/object
           scaling
           align
           fill-parent?]
    :as opts}]
  (let [image (image/create object)]
    (when (= :center align)
      (image/set-align! image align/center))
    (when (= :fill scaling)
      (image/set-scaling! image scaling/fill))
    (when fill-parent?
      (widget/set-fill-parent! image true))
    (actor/set-opts! image opts)))
