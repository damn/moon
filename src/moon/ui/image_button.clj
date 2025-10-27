(ns moon.ui.image-button
  (:require [moon.graphics.g2d.texture-region :as texture-region]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.utils.change-listener :as change-listener]
            [moon.scene2d.utils.drawable :as drawable]
            [moon.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create
  [{:keys [drawable/texture-region
           on-clicked
           drawable/scale]
    :as opts}]
  (let [scale (or scale 1)
        [w h] (texture-region/dimensions texture-region)
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! (* scale w) (* scale h)))
        image-button (ImageButton. ^Drawable drawable)]
    (when on-clicked
      (actor/add-listener! image-button (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (stage/ctx (event/stage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! image-button tooltip))
    (table/set-opts! image-button opts)))
