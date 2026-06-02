(ns editor.widget.sound.columns
  (:require [clojure.gdx.scene2d.event :as event]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [game.ctx.do :refer [do!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]))

(defn sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  ((open-select-sounds-handler table sound-columns)
                                                   (:stage/ctx (event/stage event))))}})}
   {:actor (text-button/create
            {:text "play!"
             :skin skin
             :actor/listeners {:listener/change (fn [event _actor]
                                                  (do! (:stage/ctx (event/stage event))
                                                       [[:tx/sound sound-name]]))}})}])
