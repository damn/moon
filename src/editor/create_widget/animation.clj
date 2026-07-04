(ns editor.create-widget.animation
  (:require [editor.create-widget.animation.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn f
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)]
                   {:actor (image-button/f textures image 2)})]}))
