(ns levelgen-test.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-config]
            levelgen-test.application-listener
            levelgen-test.state))

(defn start!
  [config]
  (let [{:keys [title windowed-mode foreground-fps]} (:lwjgl-app-config config)
        app-config (lwjgl3-config/new)]
    (lwjgl3-config/set-title! app-config title)
    (lwjgl3-config/set-windowed-mode! app-config (:width windowed-mode) (:height windowed-mode))
    (lwjgl3-config/set-foreground-fps! app-config foreground-fps)
    (lwjgl3-application/new (application-listener/new
                             (levelgen-test.application-listener/f levelgen-test.state/application
                                                                   config))
                            app-config)))
