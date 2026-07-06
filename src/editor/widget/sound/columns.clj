(ns editor.widget.sound.columns
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [editor.widget.sound.open-select-sounds-handler :refer [open-select-sounds-handler]]
            [ctx.do :refer [do!]]
            [gdx.scene2d.ui.text-button :as text-button]
            [gdx.scene2d.utils.change-listener :as change-listener]))

(defn sound-columns [skin table sound-name]
  [{:actor (doto (text-button/create
                  {:text sound-name
                   :skin skin})
             (actor/add-listener! (change-listener/create
                              (fn [event _actor]
                                ((open-select-sounds-handler table sound-columns)
                                 (:stage/ctx (event/get-stage event)))))))}
   {:actor (doto (text-button/create
                  {:text "play!"
                   :skin skin})
             (actor/add-listener! (change-listener/create
                              (fn [event _actor]
                                (do! (:stage/ctx (event/get-stage event))
                                     [[:tx/sound sound-name]])))))}])
