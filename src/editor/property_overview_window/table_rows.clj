(ns editor.property-overview-window.table-rows
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.set-touchable :as set-touchable]
            [scene2d.ui.stack :as stack]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.label :as label])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event Group Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [^TextureRegion texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/create)]
                (run! #(Group/.addActor stack %)
                      [(doto (ImageButton.
                              (doto (TextureRegionDrawable. texture-region)
                                (.setMinSize (* image-scale (.getRegionWidth texture-region))
                                             (* image-scale (.getRegionHeight texture-region)))))
                        (add-listener/f (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (Event/.getStage event))))))
                        (add-listener/f (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (set-touchable/f Touchable/disabled))])
                stack)})))
