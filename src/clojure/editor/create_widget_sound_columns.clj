(ns clojure.editor.create-widget-sound-columns
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.moon.ctx-do :refer [do!]]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.ui-text-button :as text-button]
            [clojure.scene2d.utils.change-listener :as change-listener]))

(defn sound-columns [skin table sound-name open-select-sounds-handler]
  [{:actor (doto (text-button/create
                   {:text sound-name
                    :skin skin})
             (actor/addListener (change-listener/create
                                      (fn [event _actor]
                                        ((open-select-sounds-handler table)
                                         (:stage/ctx (event/getStage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (actor/addListener (change-listener/create
                                      (fn [event _actor]
                                        (do! (:stage/ctx (event/getStage event))
                                             [[:tx/sound sound-name]])))))}])
