(ns clojure.editor.create-widget-s-animation
  (:require [clojure.animation-image-button :as animation-image-button]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.ui-table :as table]))

(defmethod create-widget :s/animation
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (animation-image-button/f textures image 2)})]}))
