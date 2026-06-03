(ns editor.widget.sound.open-select-sounds-handler
  (:require [clojure.gdx.scene2d.actor :refer [get-width get-height]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.scroll-pane :as scroll-pane]
            [editor.widget.sound.rebuild :refer [rebuild-sound-widget!]]
            [game.ctx.do :refer [do!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (add-actor! stage
                (window/create
                 {:title "Choose"
                  :skin skin
                  :window/close-button? skin
                  :window/modal? true
                  :table/rows
                  [[(let [table (table/create
                                 {:table/cell-defaults {:pad 5}
                                  :table/rows (for [sound-name (map first (:ctx/audio ctx))]
                                                [{:actor (doto (text-button/create
                                                                {:text sound-name
                                                                 :skin skin})
                                                           (add-listener! (change-listener/create
                                                                           (fn [event actor]
                                                                             ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (event/stage event)))))))}
                                                 {:actor (doto (text-button/create
                                                                {:text "play!"
                                                                 :skin skin})
                                                           (add-listener! (change-listener/create
                                                                           (fn [event _actor]
                                                                             (do! (:stage/ctx (event/stage event))
                                                                                  [[:tx/sound sound-name]])))))}])} )]
                      {:actor (scroll-pane/create
                               {:actor table
                                :skin skin})
                       :width  (+ (get-width table) 50)
                       :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                    (get-height table))})]]}))))
