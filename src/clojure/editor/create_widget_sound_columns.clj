(ns clojure.editor.create-widget-sound-columns
  (:require [clojure.scene2d.actor.add-listener]
            [clojure.moon.ctx-do :refer [do!]]
            [clojure.event :as event]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

(defn sound-columns [skin table sound-name open-select-sounds-handler]
  [{:actor (doto (text-button/create
                   {:text sound-name
                    :skin skin})
             (clojure.scene2d.actor.add-listener/f (change-listener/create
                                      (fn [event _actor]
                                        ((open-select-sounds-handler table)
                                         (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (clojure.scene2d.actor.add-listener/f (change-listener/create
                                      (fn [event _actor]
                                        (do! (:stage/ctx (event/get-stage event))
                                             [[:tx/sound sound-name]])))))}])
