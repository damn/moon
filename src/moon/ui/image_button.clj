(ns moon.ui.image-button
  (:require [gdl.graphics.texture-region :as texture-region]
            [gdl.ui.actor :as actor]
            [gdl.ui.event :as event]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.drawable :as drawable]
            [gdl.ui.image-button :as image-button]
            [gdl.ui.texture-region-drawable :as texture-region-drawable]
            [gdl.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.tooltip :as tooltip]))

(defn create
  [{:keys [drawable/texture-region
           on-clicked
           drawable/scale]
    :as opts}]
  (let [scale (or scale 1)
        [w h] (texture-region/dimensions texture-region)
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! (* scale w) (* scale h)))
        image-button (image-button/create drawable)]
    (when on-clicked
      (actor/add-listener! image-button (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (stage/ctx (event/stage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! image-button tooltip))
    (table/set-opts! image-button opts)))
