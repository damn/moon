(ns editor.widget.sound.columns
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [ctx.do :refer [do!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (add-listener/f (change-listener/create
                              (fn [event _actor]
                                ((open-select-sounds-handler table sound-columns)
                                 (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (add-listener/f (change-listener/create
                              (fn [event _actor]
                                (do! (:stage/ctx (event/get-stage event))
                                     [[:tx/sound sound-name]])))))}])
