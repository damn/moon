(ns editor.property-overview-window.table-rows
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.actor.set-touchable :refer [set-touchable!]]
            [clojure.scenes.scene2d.event.get-stage :refer [get-stage]]
            [clojure.scenes.scene2d.group.add-actor :refer [add-actors!]]
            [clojure.scenes.scene2d.touchable :as touchable]
            [clojure.scenes.scene2d.ui.stack :as stack]
            [clojure.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.scenes.scene2d.utils.change-listener :as change-listener]
            [clojure.scenes.scene2d.ui.image-button :as image-button]
            [clojure.scenes.scene2d.ui.label :as label]
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
