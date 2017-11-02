##Features
1. Batch based or request based project which runs 
	services on diff job boards like linkedin or dice 
		a daemon process will kick in the batch which would 
			go to each website location and find the appropriate jobs based on location and keywords 
				it will than compare those jobs with the existing job_post_id 
				if it matches, it will skip it 
				else it will recommend those job to next level 
					the next tool, would read this and scrap data from job and filter out unwanted positions and companies 
					it will assign tags to each of them 
					based on more skill matrix, it will remove or prioritize unwanted jobs
					find out top 10 jobs and send an email and applu 
					rest would be kept in csv and sent over email.
					
Request base 
	would have access to search all job listing - it would give count per job board based
	health - would provide details if access is present or can reach 
	per job board based
	provide an json to filter out positions across all job boards
	find job using post id  , if duplicate is found, it will return both
					
1. Rest service to get the job based on search criteria
2. Rest service to find personal recommendation
3. Rest service to get details for personal job location
