(ns editor.property-overview-window.table-rows
  (:require [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.actor.set-touchable :refer [set-touchable!]]
            [scene2d.event.get-stage :as get-stage]
            [scene2d.group.add-actors :refer [add-actors!]]
            [scene2d.touchable :as touchable]
            [scene2d.ui.stack :as stack]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.image-button :as image-button]
            [scene2d.ui.label :as label]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (doto (stack/create)
                (add-actors! [(doto (image-button/create
                                     (doto (texture-region-drawable/f texture-region)
                                       (set-min-size!/f (* image-scale (TextureRegion/.getRegionWidth texture-region))
                                                        (* image-scale (TextureRegion/.getRegionHeight texture-region)))))
                                (add-listener! (change-listener/create
                                                (fn [event actor]
                                                  (on-clicked actor (:stage/ctx (get-stage/f event))))))
                                (add-listener! (text-tooltip/create tooltip skin)))
                              (doto (label/create
                                     {:text extra-info-text
                                      :skin skin})
                                (set-touchable! touchable/disabled))]))})))
