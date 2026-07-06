(ns editor.property-overview-window.table-rows
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.group.add-actor :as add-actor]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [scene2d.ui.stack :as stack]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.label :as label]))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/create)]
                (run! #(add-actor/f stack %)
                      [(doto (image-button/new
                              (doto (new-texture-region-drawable/f texture-region)
                                (set-min-size/f (* image-scale (get-region-width/f texture-region))
                                                (* image-scale (get-region-height/f texture-region)))))
                        (actor/add-listener! (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (event/get-stage event))))))
                        (actor/add-listener! (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (actor/set-touchable! touchable/disabled))])
                stack)})))
