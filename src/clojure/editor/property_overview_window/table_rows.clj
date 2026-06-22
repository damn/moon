(ns clojure.editor.property-overview-window.table-rows
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.actor.set-touchable :refer [set-touchable!]]
            [clojure.event.get-stage :refer [get-stage]]
            [clojure.group.add-actors :refer [add-actors!]]
            [clojure.touchable :as touchable]
            [clojure.ui.stack :as stack]
            [clojure.ui.text-tooltip :as text-tooltip]
            [clojure.change-listener :as change-listener]
            [clojure.ui.image-button :as image-button]
            [clojure.ui.label :as label]
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
