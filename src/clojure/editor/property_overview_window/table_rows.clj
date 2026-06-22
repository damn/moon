(ns clojure.editor.property-overview-window.table-rows
  (:require [gdl.actor.add-listener :refer [add-listener!]]
            [gdl.actor.set-touchable :refer [set-touchable!]]
            [gdl.event.get-stage :refer [get-stage]]
            [gdl.group.add-actors :refer [add-actors!]]
            [gdl.touchable :as touchable]
            [gdl.ui.stack :as stack]
            [gdl.ui.text-tooltip :as text-tooltip]
            [gdl.change-listener :as change-listener]
            [gdl.ui.image-button :as image-button]
            [gdl.ui.label :as label]
            [clojure.map.texture-region-drawable :as texture-region-drawable]))

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
                                                  (on-clicked actor (:stage/ctx (get-stage event))))))
                                (add-listener! (text-tooltip/create tooltip skin)))
                              (doto (label/create
                                     {:text extra-info-text
                                      :skin skin})
                                (set-touchable! touchable/disabled))]))})))
