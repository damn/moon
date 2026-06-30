(ns editor.create-widget.animation
  (:require [clojure.gdx :as gdx]
            [gdx.scenes.scene2d.ui.table :as table]
            [moon.textures :as textures]))

(defn f
  [_ animation {:keys [ctx/textures]}]
  (table/create
   {:table/cell-defaults {:pad 1}
    :table/rows [(for [image (:animation/frames animation)
                       :let [texture-region (textures/texture-region textures image)
                             scale 2]]
                   {:actor (gdx/image-button
                            (doto (gdx/texture-region-drawable texture-region)
                              (gdx/texture-region-drawable-set-min-size!
                               (* scale (gdx/texture-region-get-region-width texture-region))
                               (* scale (gdx/texture-region-get-region-height texture-region)))))})]}))
