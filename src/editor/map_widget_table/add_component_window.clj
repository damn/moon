(ns editor.map-widget-table.add-component-window
  (:require [editor.build-widget :as build-widget]
            [editor.widget-value :as widget-value]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [scene2d.ui.window.set-modal :as set-modal]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.schemas.default-value :refer [default-value]]
            [moon.schemas.map-keys :refer [map-keys]]
            [moon.schemas.optional :refer [optional?]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event)))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (window/create
                      {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}})
                 (add-close-button/f! skin)
                 (set-modal/f! true))
        remaining-ks (sort (remove (set (keys (widget-value/f schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (Actor/.addListener (change-listener/create
                                       (fn [event _actor]
                                         (Actor/.remove window)
                                         (let [ctx (:stage/ctx (Event/.getStage event))]
                                           (add-rows! map-widget-table [((:ctx/create-component-row ctx)
                                                                          {:skin skin
                                                                           :editor-widget (build-widget/f ctx
                                                                                                          (get schemas k)
                                                                                                          k
                                                                                                          (default-value schemas k))
                                                                           :k k
                                                                           :display-remove-component-button? (optional? schemas schema k)
                                                                           :table map-widget-table})])
                                           ((:ctx/rebuild-editor-window! ctx) ctx)))))
                  )}]))
    (.pack window)
    window))
