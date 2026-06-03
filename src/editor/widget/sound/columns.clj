(ns editor.widget.sound.columns
  (:require [clojure.gdx.scene2d.actor :refer [add-listener!]]
            [clojure.gdx.scene2d.event :as event]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [game.ctx.do :refer [do!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (add-listener! (change-listener/create
                             (fn [event _actor]
                               ((open-select-sounds-handler table sound-columns)
                                (:stage/ctx (event/stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (add-listener! (change-listener/create
                             (fn [event _actor]
                               (do! (:stage/ctx (event/stage event))
                                    [[:tx/sound sound-name]])))))}])
