(ns clojure.table-rows
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [clojure.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clojure.ui-stack :as stack]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.ui-label :as label]))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/create)]
                (run! #(group/add-actor! stack %)
                      [(doto (image-button/new
                              (doto (texture-region-drawable/new texture-region)
                                (texture-region-drawable/setMinSize (* image-scale (texture-region/getRegionWidth texture-region))
                                                (* image-scale (texture-region/getRegionHeight texture-region)))))
                        (actor/addListener (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (event/getStage event))))))
                        (actor/addListener (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (actor/setTouchable touchable/disabled))])
                stack)})))
