(ns moon.ui.image-button
  (:require [moon.ui.actor :as actor]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.drawable :as drawable]
            [gdl.ui.event :as event]
            [gdl.ui.image-button :as image-button]
            [gdl.ui.stage :as stage]
            [gdl.ui.texture-region-drawable :as texture-region-drawable]
            [moon.ui.table :as table]
            [moon.ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create
  [{:keys [^TextureRegion drawable/texture-region
           on-clicked
           drawable/scale
           skin]
    :as opts}]
  (let [scale (or scale 1)
        [w h] [(.getRegionWidth  texture-region)
               (.getRegionHeight texture-region)]
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! (* scale w) (* scale h)))
        image-button (image-button/create drawable)]
    (when on-clicked
      (actor/add-listener! image-button (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (stage/ctx (event/stage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! image-button
                    tooltip
                    skin))
    (table/set-opts! image-button opts)))
