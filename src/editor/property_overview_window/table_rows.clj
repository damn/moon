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
            [map.texture-region-drawable :as texture-region-drawable]))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (doto (stack/create)
                (add-actors! [(doto (image-button/create (texture-region-drawable/create*
                                                          {:drawable/texture-region texture-region
                                                           :drawable/scale image-scale}))
                                (add-listener! (change-listener/create
                                                (fn [event actor]
                                                  (on-clicked actor (:stage/ctx (get-stage/f event))))))
                                (add-listener! (text-tooltip/create tooltip skin)))
                              (doto (label/create
                                     {:text extra-info-text
                                      :skin skin})
                                (set-touchable! touchable/disabled))]))})))
