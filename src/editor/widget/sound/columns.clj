(ns editor.widget.sound.columns
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.scenes.scene2d.event.get-stage :refer [get-stage]]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [game.ctx.do :refer [do!]]
            [clojure.scenes.scene2d.ui.text-button :as text-button]
            [clojure.scenes.scene2d.utils.change-listener :as change-listener]))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (add-listener! (change-listener/create
                             (fn [event _actor]
                               ((open-select-sounds-handler table sound-columns)
                                (:stage/ctx (get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (add-listener! (change-listener/create
                             (fn [event _actor]
                               (do! (:stage/ctx (get-stage event))
                                    [[:tx/sound sound-name]])))))}])
