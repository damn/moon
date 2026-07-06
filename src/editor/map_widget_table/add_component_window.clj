(ns editor.map-widget-table.add-component-window
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [editor.build-widget :as build-widget]
            [editor.widget-value :as widget-value]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.schemas.default-value :refer [default-value]]
            [moon.schemas.map-keys :refer [map-keys]]
            [moon.schemas.optional :refer [optional?]]))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (window/create
                      {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}})
                 (add-close-button/f! skin)
                 (gdx-window/set-modal! true))
        remaining-ks (sort (remove (set (keys (widget-value/f schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (actor/add-listener! (change-listener/create
                                   (fn [event _actor]
                                     (actor/remove! window)
                                     (let [ctx (:stage/ctx (event/get-stage event))]
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
    (layout/pack window)
    window))
