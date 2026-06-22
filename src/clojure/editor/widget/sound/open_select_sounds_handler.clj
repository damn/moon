(ns clojure.editor.widget.sound.open-select-sounds-handler
  (:require [gdl.get-height :refer [get-height]]
            [gdl.get-width :refer [get-width]]
            [gdl.add-listener :refer [add-listener!]]
            [gdl.event.get-stage :refer [get-stage]]
            [gdl.scroll-pane :as scroll-pane]
            [gdl.window.add-close-button :as add-close-button]
            [gdl.window.set-modal :as set-modal]
            [clojure.editor.widget.sound.rebuild :refer [rebuild-sound-widget!]]
            [game.ctx.do :refer [do!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdl.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdl.change-listener :as change-listener]
            [gdl.stage.add-actor :refer [add-actor!]]))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (add-actor! stage
                (doto (window/create
                       {:title "Choose"
                        :skin skin
                        :table/rows
                        [[(let [table (table/create
                                       {:table/cell-defaults {:pad 5}
                                        :table/rows (for [sound-name (map first (:ctx/audio ctx))]
                                                      [{:actor (doto (text-button/create
                                                                      {:text sound-name
                                                                       :skin skin})
                                                                 (add-listener! (change-listener/create
                                                                                 (fn [event actor]
                                                                                   ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (get-stage event)))))))}
                                                       {:actor (doto (text-button/create
                                                                      {:text "play!"
                                                                       :skin skin})
                                                                 (add-listener! (change-listener/create
                                                                                 (fn [event _actor]
                                                                                   (do! (:stage/ctx (get-stage event))
                                                                                        [[:tx/sound sound-name]])))))}])} )]
                            {:actor (scroll-pane/create
                                     {:actor table
                                      :skin skin})
                             :width  (+ (get-width table) 50)
                             :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                          (get-height table))})]]})
                  (add-close-button/f! skin)
                  (set-modal/f! true)))))
