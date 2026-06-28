(ns editor.widget.sound.columns
  (:require [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [ctx.do :refer [do!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (Actor/.addListener (change-listener/create
                                  (fn [event _actor]
                                    ((open-select-sounds-handler table sound-columns)
                                     (:stage/ctx (Event/.getStage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (Actor/.addListener (change-listener/create
                                  (fn [event _actor]
                                    (do! (:stage/ctx (Event/.getStage event))
                                         [[:tx/sound sound-name]])))))}])
