(ns editor.property-overview-window.table-rows
  (:require [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.actor.set-touchable :refer [set-touchable!]]
            [clojure.gdx.scene2d.event.get-stage :refer [get-stage]]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actors!]]
            [clojure.gdx.scene2d.touchable :as touchable]
            [clojure.gdx.scene2d.ui.stack :as stack]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.ui.image-button :as image-button]
            [clojure.gdx.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

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
