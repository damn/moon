(ns editor.widget.sound.open-select-sounds-handler
  (:require [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [editor.widget.sound.rebuild :refer [rebuild-sound-widget!]]
            [ctx.do :refer [do!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.utils.change-listener :as change-listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)
           (scene2d Stage)))

(defn open-select-sounds-handler [table ->sound-columns]
  (fn [{:keys [ctx/skin
               ctx/stage]
        :as ctx}]
    (Stage/.addActor stage
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
                                                          (Actor/.addListener (change-listener/create
                                                                               (fn [event actor]
                                                                                 ((rebuild-sound-widget! table sound-name ->sound-columns) actor (:stage/ctx (Event/.getStage event)))))))}
                                                            {:actor (doto (text-button/create
                                                                           {:text "play!"
                                                                            :skin skin})
                                                          (Actor/.addListener (change-listener/create
                                                                               (fn [event _actor]
                                                                                 (do! (:stage/ctx (Event/.getStage event))
                                                                                      [[:tx/sound sound-name]])))))}])} )]
                              {:actor (scroll-pane/create
                                       {:actor table
                                        :skin skin})
                               :width  (+ (Actor/.getWidth table) 50)
                               :height (min (- (:viewport/world-height (:stage/viewport stage)) 50)
                                            (Actor/.getHeight table))})]]})
                       (add-close-button/f! skin)
                       (.setModal true)))))
