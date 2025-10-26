(ns cdq.ui.image-button
  (:require [cdq.ui.tooltip :as tooltip]
            [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [moon.graphics.g2d.texture-region :as texture-region]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.utils.change-listener :as change-listener]
            [moon.scene2d.utils.drawable :as drawable]
            [moon.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.image-button :as image-button]))

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
