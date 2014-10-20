require 'sinatra'

set :bind, '0.0.0.0'

get '/' do
  return """
  <h1>Audio Server listening</h1>
  <pre>
    post /play/:group
  </pre>
  """
end

post '/play/:group' do  
  groupname = params[:group].gsub(/[^A-Za-z\d]/, '')

  choices = Dir["sounds/#{groupname}/*"]
  choice = choices.sample

  if choice.nil?
    status 404
    return
  end

  playSound(choice)

  return
end

def playSound(file)
  puts "Playsound: #{file}"
  `mplayer '#{file}' -af volume=10`
end