(ns editor.map-widget-table.add-component-window
  (:require [com.badlogic.gdx.scenes.scene2d.event.get-stage :refer [get-stage]]
            [editor.widget :as widget]
            [com.badlogic.gdx.scenes.scene2d.actor.remove :refer [remove!]]
            [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table.add-rows :refer [add-rows!]]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.utils.layout.pack :refer [pack!]]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.schemas.default-value :refer [default-value]]
            [moon.schemas.map-keys :refer [map-keys]]
            [moon.schemas.optional :refer [optional?]]))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (window/create
                {:title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (widget/value schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (add-listener! (change-listener/create
                                  (fn [event _actor]
                                    (remove! window)
                                    (let [ctx (:stage/ctx (get-stage event))]
                                      (add-rows! map-widget-table [((:ctx/create-component-row ctx)
                                                                    {:skin skin
                                                                     :editor-widget (widget/build ctx
                                                                                                  (get schemas k)
                                                                                                  k
                                                                                                  (default-value schemas k))
                                                                     :k k
                                                                     :display-remove-component-button? (optional? schemas schema k)
                                                                     :table map-widget-table})])
                                      ((:ctx/rebuild-editor-window! ctx) ctx)))))
                  )}]))
    (pack! window)
    window))
