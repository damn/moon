(ns cdq.ui.image
  (:require [cdq.ui.actor :as actor]
            [moon.scene2d.ui.image :as image]
            [moon.scene2d.ui.widget :as widget]
            [moon.utils.align :as align]
            [moon.utils.scaling :as scaling]
            [moon.ui.image :as vis-image]))

(defn create
  [{:keys [image/object
           scaling
           align
           fill-parent?]
    :as opts}]
  (let [image (vis-image/create object)]
    (when (= :center align)
      (image/set-align! image align/center))
    (when (= :fill scaling)
      (image/set-scaling! image scaling/fill))
    (when fill-parent?
      (widget/set-fill-parent! image true))
    (actor/set-opts! image opts)))
